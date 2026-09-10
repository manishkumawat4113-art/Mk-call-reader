const statusText =
    document.getElementById("status");

const enableBtn =
    document.getElementById("enableBtn");

const testBtn =
    document.getElementById("testBtn");

const voiceSwitch =
    document.getElementById("voiceSwitch");


/*
    Native Android bridge
*/

function getAndroidReader() {

    if (
        window.MKCallReader &&
        typeof window.MKCallReader === "object"
    ) {

        return window.MKCallReader;

    }

    return null;

}


/*
    Enable Accessibility Service
*/

enableBtn.addEventListener(
    "click",
    () => {

        const reader =
            getAndroidReader();

        if (!reader) {

            statusText.textContent =
                "Android service is not available.";

            return;

        }

        reader.openAccessibilitySettings();

    }
);


/*
    Test voice
*/

testBtn.addEventListener(
    "click",
    () => {

        const reader =
            getAndroidReader();

        if (reader) {

            reader.speak({
                text:
                    "MK Call Reader is working"
            });

            return;
        }


        /*
            Browser fallback
        */

        const speech =
            new SpeechSynthesisUtterance(
                "MK Call Reader is working"
            );

        speech.lang =
            "en-IN";

        window.speechSynthesis.speak(
            speech
        );

    }
);


/*
    Voice setting
*/

voiceSwitch.addEventListener(
    "change",
    () => {

        const reader =
            getAndroidReader();

        if (!reader) return;


        reader.setVoiceEnabled({
            enabled:
                voiceSwitch.checked
        });

    }
);


/*
    Initial status
*/

window.addEventListener(
    "load",
    () => {

        const reader =
            getAndroidReader();

        if (reader) {

            statusText.textContent =
                "Android reader is ready.";

        } else {

            statusText.textContent =
                "Open this app on Android.";

        }

    }
);
