const { createApp } = Vue;

const options = {
    data() {
        return {
            clientAccounts: [],
            destinataryType: "Others",
            destinatary: "",
            origin: "",
            amount: "",
            description: "",
            errorMessage: "",
            moneyFormatter: {}
        }
    },
    created(){
        this.loadCurrentClient();

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        })
    },
    methods: {
        loadCurrentClient() {
            axios.get('/api/clients/current/accounts')
                .then(res => {
                    this.clientAccounts = res.data;
                })
                .catch(err => console.error(err))
        },
        sendTransaction(){
        const amountRegex = new RegExp(/^[0-9]{1,3}(?:,?[0-9]{3})*\.[0-9]{2}$/);

        if(this.amount == "") {
            this.errorMessage = "Please enter an amount for the transaction";
            return
        }
        if(!(amountRegex.test(this.amount))) {
            this.errorMessage = "Please enter a numeric amount with the next format: 1000.00";
            return
        }
        if(this.destinataryType == "Others" && this.origin == this.destinatary) {
            this.errorMessage = "You can't transfer money to the same account";
            return
        }

            this.errorMessage = "";
            Swal.fire({
              title: 'Are you sure you want to make this transaction?',
              icon: 'info',
              buttonsStyling: false,
              customClass: {
                confirmButton: 'btn primary-btn btn-lg mb-3 mb-md-0',
                cancelButton: 'btn secondary-btn btn-lg me-md-5 mb-3 mt-2 my-md-2'
              },
              showCancelButton: true,
              confirmButtonText: 'Yes',
              cancelButtonText: 'Cancel',
              reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    axios.post('/api/transactions', `amount=${this.amount}&description=${this.description}&accountOrigin=${this.origin}&accountDestination=${this.destinatary}`)
                        .then(res => {
                                 Swal.fire({
                                      position: 'center',
                                      icon: 'success',
                                      title: 'Your transaction has been made!',
                                      showConfirmButton: true,
                                      buttonsStyling: false,
                                      customClass: {
                                        confirmButton: 'btn primary-btn btn-lg',
                                      }
                                 })
                                 .then(result => {
                                      if (result.isConfirmed) {
                                           document.location.reload()
                                      }
                                 })
                                this.destinatary = "";
                                this.origin = "";
                                this.amount = null;
                                this.description = "";
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
            }})
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