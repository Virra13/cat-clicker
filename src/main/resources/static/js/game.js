let game = null;

let lastServerActivity = Date.now();

const MAX_PASSIVE_SECONDS = 15 * 60;


const stageOrder = {
    BEGINNER: 0,
    JUNIOR: 1,
    MIDDLE: 2,
    SENIOR: 3,
    TECH_LEAD: 4,
    CTO: 5
};


const programs = [
    {
        type: "HELLO_WORLD",
        name: "Hello World",
        lines: 100,
        money: 50,
        stage: "BEGINNER"
    },
    {
        type: "TODO_APP",
        name: "TODO App",
        lines: 500,
        money: 300,
        stage: "JUNIOR"
    },
    {
        type: "ONLINE_STORE",
        name: "Интернет-магазин",
        lines: 2000,
        money: 1500,
        stage: "MIDDLE"
    },
    {
        type: "SOCIAL_NETWORK",
        name: "Социальная сеть",
        lines: 10000,
        money: 10000,
        stage: "SENIOR"
    }
];


const keyboardPrices = {
    BASIC: {
        next: "MECHANICAL",
        price: 100
    },

    MECHANICAL: {
        next: "GAMING",
        price: 500
    },

    GAMING: {
        next: "PROFESSIONAL",
        price: 2000
    },

    PROFESSIONAL: {
        next: "ABSOLUTE_NONSENSE",
        price: 10000
    },

    ABSOLUTE_NONSENSE: null
};


const keyboardRequiredStage = {
    MECHANICAL: "BEGINNER",
    GAMING: "JUNIOR",
    PROFESSIONAL: "MIDDLE",
    ABSOLUTE_NONSENSE: "SENIOR"
};


const aiPrices = {
    FREE_AI: 300,
    AI_PRO: 1500,
    VIBE_CODING: 5000
};


const aiRequiredStage = {
    FREE_AI: "BEGINNER",
    AI_PRO: "JUNIOR",
    VIBE_CODING: "MIDDLE"
};


const currentLinesElement =
    document.getElementById("current-lines");

const totalLinesElement =
    document.getElementById("total-lines");

const moneyElement =
    document.getElementById("money");

const linesPerClickElement =
    document.getElementById("lines-per-click");

const linesPerSecondElement =
    document.getElementById("lines-per-second");

const stageElement =
    document.getElementById("stage");

const keyboardElement =
    document.getElementById("keyboard");

const monitorCountElement =
    document.getElementById("monitor-count");

const aiElement =
    document.getElementById("ai");

const aiTimeElement =
    document.getElementById("ai-time");

const messageElement =
    document.getElementById("message");


const catButton =
    document.getElementById("cat-button");

const buyKeyboardButton =
    document.getElementById("buy-keyboard");

const buyMonitorButton =
    document.getElementById("buy-monitor");

const buyFreeAiButton =
    document.getElementById("buy-free-ai");


async function initGame() {

    const gameId = localStorage.getItem("gameId");

    if (!gameId
        || gameId === "undefined"
        || gameId === "null") {

        localStorage.removeItem("gameId");

        await createGame();

    } else {

        await loadGame(gameId);
    }

    renderGame();
}


async function createGame() {

    const response = await fetch(
        "/api/games",
        {
            method: "POST"
        }
    );

    if (!response.ok) {
        showMessage("Не удалось создать игру.");
        return;
    }

    game = await response.json();

    localStorage.setItem(
        "gameId",
        game.id
    );

    lastServerActivity = Date.now();
}


async function loadGame(gameId) {

    const response = await fetch(
        `/api/games/${gameId}`
    );

    if (response.status === 404) {

        localStorage.removeItem("gameId");

        await createGame();

        return;
    }

    if (!response.ok) {
        showMessage("Не удалось загрузить игру.");
        return;
    }

    game = await response.json();

    lastServerActivity = Date.now();
}


async function clickCat() {

    const response = await fetch(
        `/api/games/${game.id}/click`,
        {
            method: "POST"
        }
    );

    if (!response.ok) {
        return;
    }

    game = await response.json();

    lastServerActivity = Date.now();

    renderGame();
}


async function sellProgram(programType) {

    const response = await fetch(
        `/api/games/${game.id}/programs/${programType}/sell`,
        {
            method: "POST"
        }
    );

    if (!response.ok) {
        showMessage("Кот пока не способен продать эту программу.");
        return;
    }

    game = await response.json();

    lastServerActivity = Date.now();

    showMessage("Программа продана. Капитализм победил.");

    renderGame();
}


async function buyUpgrade(type) {

    const response = await fetch(
        `/api/games/${game.id}/upgrades/${type}/buy`,
        {
            method: "POST"
        }
    );

    if (!response.ok) {
        return;
    }

    game = await response.json();

    lastServerActivity = Date.now();

    renderGame();
}


async function buyAi(tier) {

    const response = await fetch(
        `/api/games/${game.id}/ai/${tier}/buy`,
        {
            method: "POST"
        }
    );

    if (!response.ok) {
        return;
    }

    game = await response.json();

    lastServerActivity = Date.now();

    renderGame();
}


function renderGame() {

    if (!game) {
        return;
    }


    currentLinesElement.textContent =
        Math.floor(game.currentLines);

    totalLinesElement.textContent =
        Math.floor(game.totalLines);

    moneyElement.textContent =
        game.money;

    linesPerClickElement.textContent =
        game.linesPerClick;

    linesPerSecondElement.textContent =
        game.linesPerSecond;

    stageElement.textContent =
        game.stage;

    keyboardElement.textContent =
        game.keyboard;

    monitorCountElement.textContent =
        game.monitorCount;

    aiElement.textContent =
        game.ai;


    renderPrograms();

    renderKeyboard();

    renderMonitor();

    renderAi();
}


function renderPrograms() {

    const container =
        document.getElementById("programs");

    container.innerHTML = "";


    programs.forEach(program => {

        const availableByStage =
            stageOrder[game.stage]
            >= stageOrder[program.stage];

        const enoughLines =
            game.currentLines >= program.lines;


        const row =
            document.createElement("div");

        row.className = "program-row";


        const text =
            document.createElement("span");

        text.textContent =
            `${program.name} — `
            + `${program.lines} строк → `
            + `${program.money} денег`;


        const button =
            document.createElement("button");


        if (!availableByStage) {

            button.textContent =
                `Откроется на ${program.stage}`;

            button.disabled = true;

        } else {

            button.textContent = "Продать";

            button.disabled = !enoughLines;

            button.addEventListener(
                "click",
                () => sellProgram(program.type)
            );
        }


        row.appendChild(text);
        row.appendChild(button);

        container.appendChild(row);
    });
}


function renderKeyboard() {

    const current =
        keyboardPrices[game.keyboard];


    if (current === null) {

        buyKeyboardButton.textContent =
            "Клавиатура максимального уровня";

        buyKeyboardButton.disabled = true;

        return;
    }


    const nextKeyboard =
        current.next;

    const requiredStage =
        keyboardRequiredStage[nextKeyboard];


    const stageAllowed =
        stageOrder[game.stage]
        >= stageOrder[requiredStage];


    buyKeyboardButton.textContent =
        `Купить ${nextKeyboard} — ${current.price}`;


    buyKeyboardButton.disabled =
        game.money < current.price
        || !stageAllowed;
}


function renderMonitor() {

    buyMonitorButton.disabled =
        game.monitorCount >= 6
        || game.money < 300;


    if (game.monitorCount >= 6) {

        buyMonitorButton.textContent =
            "Больше мониторов кот физически не видит";

    } else {

        buyMonitorButton.textContent =
            "Купить монитор — 300";
    }
}


function renderAi() {

    if (!game.freeAiUnlocked) {

        buyFreeAiButton.disabled =
            game.money < aiPrices.FREE_AI;

        buyFreeAiButton.textContent =
            "Купить FREE AI — 300";

    } else {

        buyFreeAiButton.disabled = true;

        buyFreeAiButton.textContent =
            "FREE AI куплен";
    }


    const subscriptionActive =
        game.aiSubscriptionExpiresAt !== null
        && new Date(game.aiSubscriptionExpiresAt)
        > new Date();


    document
        .querySelectorAll(".ai-subscription")
        .forEach(button => {

            const tier =
                button.dataset.tier;

            const stageAllowed =
                stageOrder[game.stage]
                >= stageOrder[aiRequiredStage[tier]];


            button.disabled =
                subscriptionActive
                || !game.freeAiUnlocked
                || !stageAllowed
                || game.money < aiPrices[tier];
        });


    renderAiTimer();
}


function renderAiTimer() {

    if (!game.aiSubscriptionExpiresAt) {

        aiTimeElement.textContent =
            game.freeAiUnlocked
                ? "работает FREE AI"
                : "AI не подключён";

        return;
    }


    const expires =
        new Date(
            game.aiSubscriptionExpiresAt
        ).getTime();


    const seconds =
        Math.max(
            0,
            Math.floor(
                (expires - Date.now()) / 1000
            )
        );


    if (seconds <= 0) {

        aiTimeElement.textContent =
            "платная подписка закончилась";

        return;
    }


    const minutes =
        Math.floor(seconds / 60);

    const remainingSeconds =
        seconds % 60;


    aiTimeElement.textContent =
        `${minutes}:`
        + `${remainingSeconds.toString().padStart(2, "0")}`;
}


function showMessage(text) {
    messageElement.textContent = text;
}


catButton.addEventListener(
    "click",
    clickCat
);


buyKeyboardButton.addEventListener(
    "click",
    () => buyUpgrade("KEYBOARD")
);


buyMonitorButton.addEventListener(
    "click",
    () => buyUpgrade("MONITOR")
);


buyFreeAiButton.addEventListener(
    "click",
    () => buyAi("FREE_AI")
);


document
    .querySelectorAll(".ai-subscription")
    .forEach(button => {

        button.addEventListener(
            "click",
            () => buyAi(button.dataset.tier)
        );
    });


setInterval(() => {

    if (!game) {
        return;
    }

    const idleSeconds =
        Math.floor(
            (Date.now() - lastServerActivity) / 1000
        );


    if (idleSeconds < MAX_PASSIVE_SECONDS
        && game.linesPerSecond > 0) {

        game.currentLines +=
            game.linesPerSecond;

        game.totalLines +=
            game.linesPerSecond;
    }


    renderGame();

}, 1000);


initGame();
