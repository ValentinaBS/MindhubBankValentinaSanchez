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
        .catch(err => console.error(err))

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
           style: 'currency',
           currency: 'USD'
        })
    }
}

const app = createApp(options);
app.mount("#app")