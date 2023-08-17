const { createApp } = Vue;

const options = {
    data(){
        return {
            client: {},
            clientCards: [],
            cardsDebit: [],
            cardsCredit: [],
            dateFormatter: {}
        }
    },
    created(){
        axios.get("/api/clients/1")
        .then(res => {
           this.client = res.data;
           this.clientCards = this.client.cards;
           this.cardsDebit = this.clientCards.filter(card => card.type == "DEBIT");
           this.cardsCredit = this.clientCards.filter(card => card.type == "CREDIT");
           console.log(this.cardsDebit)
           console.log(this.cardsCredit)
        })
        .catch(err => console.error(err))

        this.dateFormatter = new Intl.DateTimeFormat('en-US', {
            month: '2-digit',
            year: '2-digit'
        })
    }
}

const app = createApp(options);
app.mount("#app")