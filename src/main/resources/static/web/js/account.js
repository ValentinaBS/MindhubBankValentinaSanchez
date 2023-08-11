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
    },
    methods: {
              formatDate(date){
                   let newDate = new Date(date)
                   let year = newDate.getFullYear()
                   let day = newDate.getDay()
                   let arrayYear = Array.from(year.toString()).slice(-2).join("")
                   let month = newDate.getMonth() +1
                   if(month < 10){
                        month = "0" + month
                   }
                   let timeHours = newDate.getHours()
                   let timeMinutes = newDate.getMinutes()
                   if(timeHours < 10) {
                        timeHours = "0" + timeHours
                   }
                   if(timeMinutes < 10) {
                        timeMinutes = "0" + timeMinutes
                   }
                   let dayMonthYearTime = day + "/" + month + "/" + arrayYear + "-" + timeHours + ":" + timeMinutes
                   return dayMonthYearTime
              }
    }
}

const app = createApp(options);
app.mount("#app")