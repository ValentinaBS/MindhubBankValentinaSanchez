const { createApp } = Vue;

const options = {
    data(){
        return {
            client: {},
            firstName: "",
            clientAccounts: [],
            clientLoans: []
        }
    },
    created(){
        this.loadCurrentClient();

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        })
    },
    methods: {
        loadCurrentClient() {
        axios.get('/api/clients/current')
            .then(res => {
                this.client = res.data;
                this.firstName = this.client.firstName;
                this.clientAccounts = this.client.accounts;
                this.clientLoans = this.client.loans
            })
            .catch(err => console.error(err))
        },
        createAccount(){
            axios.post('/api/clients/current/accounts')
                .then(() => {
                    this.loadCurrentClient();
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
        },
        logOut() {
            axios.post('/api/logout')
            .then(window.location.href = '/web/index.html')
        }
    }
}

const app = createApp(options);
app.mount("#app")