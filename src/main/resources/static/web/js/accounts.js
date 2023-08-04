const { createApp } = Vue;

const options = {
    data(){
        return {
            client: {},
            firstName: "",
            clientAccounts: [],
        }
    },
    created(){
        axios.get("/api/clients/1")
        .then(res => {
            this.client = res.data;
            this.firstName = this.client.firstName;
            this.clientAccounts = this.client.accounts;
        })
        .catch(err => console.error(err))
    }
}

const app = createApp(options);
app.mount("#app")