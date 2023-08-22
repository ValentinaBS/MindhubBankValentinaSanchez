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
        axios.get("/api/clients/current")
        .then(res => {
            this.client = res.data;
            this.firstName = this.client.firstName;
            this.clientAccounts = this.client.accounts;
            this.clientLoans = this.client.loans
        })
        .catch(err => console.error(err))

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        })
    },
    methods: {
        logOut() {
            axios.post('/api/logout')
            .then(window.location.href = '/web/index.html')
        }
    }
}

const app = createApp(options);
app.mount("#app")