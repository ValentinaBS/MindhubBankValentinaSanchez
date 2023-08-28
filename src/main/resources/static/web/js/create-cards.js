const { createApp } = Vue;

const options = {
    data() {
        return {
            cardType: "EMPTY",
            cardColor: "EMPTY",
            errorMessage: ""
        }
    },
    methods: {
        createCard() {
            axios.post('/api/clients/current/cards', `color=${this.cardColor}&type=${this.cardType}`)
                .then(res => {
                    console.log(res)
                    window.location.href = '/web/pages/cards.html'
                    this.cardType = "EMPTY";
                    this.cardColor = "EMPTY";
                })
                .catch(error => {
                    if (error.response) {
                        console.log(error.response.data);
                        console.log(error.response.status);
                        console.log(error.response.headers);
                        this.errorMessage = error.response.data;
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