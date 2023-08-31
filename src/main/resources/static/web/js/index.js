const { createApp } = Vue;

const options = {
    data() {
        return {
            emailInput: "",
            passwordInput: "",
            fNameInput: "",
            lNameInput: "",
            currentClient: {},
            showErrorMessage: false,
            errorMessage: "",
            isSignUpSelected: false,
            isPasswordShowing: false
        }
    },
    created() {
    },
    methods: {
        loginOrSignIn() {
            this.isSignUpSelected = !(this.isSignUpSelected);
            this.showErrorMessage = false;
        },
        showPassword() {
            this.isPasswordShowing = !(this.isPasswordShowing);
        },
        submitLogin() {
            axios.post("/api/login", `email=${this.emailInput}&password=${this.passwordInput}`)
                .then(res => {
                    if (this.emailInput.startsWith("admin")) {
                        window.location.href = "/web/adminPages/manager.html"
                    } else {
                        window.location.href = "/web/pages/accounts.html"
                    }
                    this.emailInput = "";
                    this.passwordInput = "";
                    this.showErrorMessage = false;
                })
                .catch(error => {
                    if (error.response) {
                        // The request was made and the server responded with a status code
                        // that falls out of the range of 2xx
                        console.log(error.response.data);
                        console.log(error.response.status);
                        console.log(error.response.headers);
                        this.showErrorMessage = true;
                        this.errorMessage = "Incorrect email or password.";
                    } else if (error.request) {
                        // The request was made but no response was received
                        // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
                        // http.ClientRequest in node.js
                        console.log(error.request);
                    } else {
                        // Something happened in setting up the request that triggered an Error
                        console.log('Error', error.message);
                    }
                    console.log(error.config);
                })
        },
        submitSignUp() {
            axios.post("/api/clients", `email=${this.emailInput}&password=${this.passwordInput}&firstName=${this.fNameInput}&lastName=${this.lNameInput}`)
                .then(() => {
                    this.submitLogin();
                    this.firstName = "";
                    this.lastName = "";
                })
                .catch(error => {
                    if (error.response) {
                        console.log(error.response.data);
                        console.log(error.response.status);
                        console.log(error.response.headers);
                        this.showErrorMessage = true;
                        this.errorMessage = error.response.data;
                    } else if (error.request) {
                        console.log(error.request);
                    } else {
                        console.log('Error', error.message);
                    }
                    console.log(error.config);
                })
        }
    }
}

const app = createApp(options);
app.mount("#app")