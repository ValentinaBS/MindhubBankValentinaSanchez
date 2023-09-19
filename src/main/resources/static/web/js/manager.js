const { createApp } = Vue;

const options = {
    data(){
        return {
            clientList: [],
            restResponse: null,
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            errorMessage: "",
            isPasswordShowing: false
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

            if(this.firstName == "" || this.lastName == "" || this.email == "" || this.password == "") {
                this.errorMessage = "Please don't leave any empty fields."
                return
            }

            axios.post('/api/clients', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`)
            .then(() => {
                document.location.reload();
                this.errorMessage = "";
                this.firstName = "";
                this.lastName = "";
                this.email = "";
                this.password = "";
            })
            .catch(err => {
                this.errorMessage = err.response.data;
            })
        },
        showPassword() {
            this.isPasswordShowing = !(this.isPasswordShowing);
        },
        logOut() {
            Swal.fire({
                title: 'Are you sure you want to log out?',
                icon: 'warning',
                buttonsStyling: false,
                customClass: {
                    confirmButton: 'btn primary-btn btn-lg',
                    cancelButton: 'btn secondary-btn btn-lg me-4'
                },
                showCancelButton: true,
                confirmButtonText: 'Log out',
                cancelButtonText: 'Cancel',
                reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    axios.post('/api/logout')
                        .then(window.location.href = '/web/index.html')
                }
            })
        }
    }
}

const app = createApp(options);
app.mount("#app")