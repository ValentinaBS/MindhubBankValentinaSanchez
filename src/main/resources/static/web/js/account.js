const { createApp } = Vue;

const options = {
    data(){
        return {
            accountId: 1,
            chosenAccount: {},
            transactions: [],
            urlParams: {},
            debitClass: 'debit',
            creditClass: 'credit'
        }
    },
    created(){
        this.urlParams = new URLSearchParams(location.search);
        this.accountId = this.urlParams.get("id");

        axios.get(`/api/accounts/${this.accountId}`)
        .then(res => {
            this.chosenAccount = res.data;
            this.transactions = this.chosenAccount.transactions.sort((a, b) => a.id - b.id);
            console.log(this.transactions)
        })
        .catch(err => console.error(err))
    }
}

const app = createApp(options);
app.mount("#app")