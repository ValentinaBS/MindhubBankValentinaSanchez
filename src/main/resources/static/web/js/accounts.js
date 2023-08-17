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
        axios.get("/api/clients/1")
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
    }
}

const app = createApp(options);
app.mount("#app")