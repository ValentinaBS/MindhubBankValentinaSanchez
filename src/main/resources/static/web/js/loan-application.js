const { createApp } = Vue;

const options = {
    data() {
        return {
            currentClient: [],
            clientAccounts: [],
            currentClientLoans: [],
            allLoans: [],
            availableLoans: [],
            selectedLoan: [],
            selectedLoanInstallments: [],

            inputLoan: "",
            destinatary: "",
            amount: "",
            installments: "",

            errorMessage: "",
            moneyFormatter: {}
        }
    },
    async created() {
      try {
        // Use Promise.all to await multiple Axios requests concurrently
        const [loansResponse, currentClientResponse, accountsResponse] = await Promise.all([
          axios.get('/api/loans'),
          axios.get('/api/clients/current'),
          axios.get('/api/clients/current/accounts')
        ]);

        // Handle loans response
        this.allLoans = loansResponse.data;
        console.log(this.allLoans);

        // Handle current client loans response
        this.currentClient = currentClientResponse.data;
        this.currentClientLoans = this.currentClient.loans;
        console.log(this.currentClientLoans);

        // Handle accounts response
        this.clientAccounts = accountsResponse.data;
        console.log(this.clientAccounts);

        // Now that all data is loaded, update availableLoans
        this.availableLoans = this.allLoans.filter(loan => !this.currentClientLoans.some(clientLoan => clientLoan.name === loan.name));

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
          style: 'currency',
          currency: 'USD'
        });

      } catch (error) {
        console.error(error);
      }
    },
    methods: {
        showSelectedInstallments() {
            this.selectedLoan = this.allLoans.find(loan => loan.name == this.inputLoan);
            this.selectedLoanInstallments = this.selectedLoan.payments.sort((a, b) => a - b);
        },
        sendLoanRequest() {
            const amountRegex = new RegExp(/^[0-9]+\.[0-9]{2}$/);

            if (this.inputLoan == "") {
                this.errorMessage = "Please enter a type of loan";
                return
            }
            if (this.destinatary == "") {
                this.errorMessage = "Please enter an account to deposit the loan";
                return
            }
            if (this.installments == "") {
                this.errorMessage = "Please enter a number of installments";
                return
            }
            if (this.amount == "") {
                this.errorMessage = "Please enter an amount for the loan";
                return
            }
            if (!(amountRegex.test(this.amount))) {
                this.errorMessage = "Please enter a numeric amount with the next format: 1000.00";
                return
            }

            this.errorMessage = "";
            Swal.fire({
                title: 'Are you sure you want to ask for this loan?',
                text: "You won't be able to revert this!",
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
                    axios.post('/api/loans', {id: this.selectedLoan.id, amount: this.amount, payments: this.installments, destinataryAccountNumber: this.destinatary})
                        .then(res => {
                            Swal.fire({
                                position: 'center',
                                icon: 'success',
                                title: 'Your loan has been approved!',
                                showConfirmButton: true,
                                buttonsStyling: false,
                                customClass: {
                                    confirmButton: 'btn primary-btn btn-lg',
                                }
                            })
                                .then(result => {
                                    if (result.isConfirmed) {
                                        window.location.href = '/web/pages/accounts.html'
                                    }
                                })
                            this.inputLoan = "";
                            this.destinatary = "";
                            this.amount = "";
                            this.installments = "";
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
                }
            })
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