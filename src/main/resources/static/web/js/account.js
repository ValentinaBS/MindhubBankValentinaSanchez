const { createApp } = Vue;

const options = {
    data(){
        return {
            accountId: 1,
            chosenAccount: {},
            transactions: [],
            urlParams: {},
            debitClass: 'debit',
            creditClass: 'credit',
            moneyFormatter: {}
        }
    },
    created(){
        this.urlParams = new URLSearchParams(location.search);
        this.accountId = this.urlParams.get("id");

        axios.get(`/api/accounts/${this.accountId}`)
        .then(res => {
            this.chosenAccount = res.data;
            this.transactions = this.chosenAccount.transactions.sort((a, b) => a.id - b.id);
        })
        .catch(err => {
            window.location.href = '/web/pages/accounts.html'
        })

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