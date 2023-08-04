const { createApp } = Vue;

const options = {
    data(){
        return {
            clientList: [],
            restResponse: null,
            firstName: "",
            lastName: "",
            email: ""
        }
    },
    created(){
        this.loadData()
    },
    methods:{
        loadData(){
            axios.get('/rest/clients')
            .then(res => {
                this.restResponse = res.data;
                this.clientList = res.data._embedded.clients;
            })
            .catch(err => console.error(err))
        },
        addClient(){
            if(this.firstName != "" && this.lastName != "" && this.email != "") {
                this.postClient()
            }
        },
        postClient(){
            axios.post('/rest/clients', {
                firstName: this.firstName,
                lastName: this.lastName,
                email: this.email
            })
            .then(res => this.loadData())
            .catch(err => console.error(err))
        }
    }
}

const app = createApp(options);
app.mount("#app")