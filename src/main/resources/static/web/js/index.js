const { createApp } = Vue;

const options = {
    data(){
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
    created(){
    },
    methods: {
        loginOrSignIn() {
            this.isSignUpSelected = !(this.isSignUpSelected);
            this.showErrorMessage = false;
        },
        submitLogin(){
            axios.post("/api/login",`email=${this.emailInput}&password=${this.passwordInput}`)
                .then(res => {
                    this.roleChecker();
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
        submitSignUp(){
            const emailRegex = new RegExp(/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/);
            const passRegex = new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)

            if(this.fNameInput == "" || this.lNameInput == "" || this.emailInput == "" || this.passwordInput == "") {
                this.errorMessage = "Please don't leave any empty fields."
                this.showErrorMessage = true;
                return;
            } else if(!(emailRegex.test(this.emailInput))){
                this.errorMessage = 'Your email address must be in the next format: email@example.com'
                this.showErrorMessage = true;
                return;
            } else if(!(passRegex.test(this.passwordInput))){
                this.errorMessage = 'Your password must have between 8-15 characters, one uppercase letter, one lowercase letter, one number and one special character.'
                this.showErrorMessage = true;
                return;
            } else {
                axios.post("/api/clients",`email=${this.emailInput}&password=${this.passwordInput}&firstName=${this.fNameInput}&lastName=${this.lNameInput}`)
                    .then(() => {
                        this.submitLogin();
                        this.emailInput = "";
                        this.passwordInput = "";
                        this.firstName = "";
                        this.lastName = "";
                    })
                    .catch(error => {
                        if (error.response) {
                            console.log(error.response.data);
                            console.log(error.response.status);
                            console.log(error.response.headers);
                        } else if (error.request) {
                            console.log(error.request);
                        } else {
                            console.log('Error', error.message);
                        }
                        console.log(error.config);
                    })
            }
        },
        roleChecker(){
            axios.get("/api/clients/current")
                .then(res => {
                    this.currentClient = res.data;
                    if(this.currentClient.firstName == "admin" && this.currentClient.email.includes("admin")){
                        window.location.href = "/web/adminPages/manager.html"
                    }else{
                        window.location.href = "/web/pages/accounts.html"
                    }}
                )
        },
        logOut() {
            axios.post('/api/logout')
            .then(window.location.href = '/web/index.html')
        },
        showPassword() {
            this.isPasswordShowing = !(this.isPasswordShowing);
        }
    },
    computed: {
        checkLoggedUser() {
            this.roleChecker();
        }
    }
}

const app = createApp(options);
app.mount("#app")