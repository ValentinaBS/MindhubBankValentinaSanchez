const { createApp } = Vue;

const options = {
    data() {
        return {
            client: {},
            firstName: "",
            clientAccounts: [],
            clientLoans: [],
            moneyFormatter: {},
            accountType: "CHECKING",
            selectedLoanToPay: {},
            loanAccount: null,
            payLoanConfirmation: false,
            errorMessage: ""
        }
    },
    created() {
        this.loadCurrentClient();

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        })
    },
    methods: {
        loadCurrentClient() {
            axios.get('/api/clients/current')
                .then(res => {
                    this.client = res.data;
                    this.firstName = this.client.firstName;
                    this.clientAccounts = this.client.accounts.filter(account => account.active);
                    this.clientLoans = this.client.loans.filter(loan => loan.active);
                })
                .catch(err => console.error(err))
        },
        createAccount() {
            Swal.fire({
                title: 'Are you sure you want to create a new account?',
                icon: 'info',
                buttonsStyling: false,
                customClass: {
                    confirmButton: 'btn primary-btn btn-lg mb-3 mb-md-0',
                    cancelButton: 'btn secondary-btn btn-lg me-md-5 mb-3 mt-2 my-md-2'
                },
                showCancelButton: true,
                confirmButtonText: 'Yes, create an account',
                cancelButtonText: 'Cancel',
                reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    axios.post('/api/clients/current/accounts', `type=${this.accountType}`)
                    .then(() => {
                        document.location.reload()
                    })
                    .catch(error => {
                        if (error.response) {
                            console.log(error.response.data);
                            console.log(error.response.status);
                            console.log(error.response.headers);
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
        removeAccount(accountId){
            Swal.fire({
                title: 'Are you sure you want to delete this account?',
                icon: 'warning',
                buttonsStyling: false,
                customClass: {
                    confirmButton: 'btn primary-btn btn-lg mb-3 mb-md-0',
                    cancelButton: 'btn secondary-btn btn-lg me-md-5 mb-3 mt-2 my-md-2'
                },
                showCancelButton: true,
                confirmButtonText: 'Yes, delete this account',
                cancelButtonText: 'Cancel',
                reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    axios.patch(`/api/clients/current/accounts/${accountId}`)
                    .then(res => {
                        Swal.fire({
                            position: 'center',
                            icon: 'success',
                            title: 'Your account has been deleted!',
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
                    })
                    .catch(error => {
                        Swal.fire({
                            icon: 'error',
                            title: 'Oh no!',
                            text: `${error.response.data}`,
                            confirmButtonText: 'Ok',
                            buttonsStyling: false,
                            customClass: {
                                confirmButton: 'btn primary-btn btn-lg',
                            }
                        })
                        console.log(error.response.data);
                        console.log(error.response.status);
                        console.log(error.response.headers);
                    })
                }
            })
        },
        payLoan() {
            axios.patch(`/api/loans/${this.selectedLoanToPay.id}`, `accountNumber=${this.loanAccount}`)
            .then(() => {
                document.location.reload();
                this.resetMessages()
            })
            .catch(error => {
                if (error.response) {
                    console.log(error.response.data);
                    console.log(error.response.status);
                    console.log(error.response.headers);
                    this.errorMessage = error.response.data;
                    this.payLoanConfirmation = false;
                } else if (error.request) {
                    console.log(error.request);
                } else {
                    console.log('Error', error.message);
                }
                console.log(error.config);
            })
        },
        selectedLoan(loan) {
            this.selectedLoanToPay = loan;
        },
        confirmPay(account) {
            if(account == null) {
                this.errorMessage = "You must select an account to pay."
                return
            }
            this.errorMessage = "";
            this.payLoanConfirmation = true;
        },
        resetMessages() {
            this.errorMessage = "";
            this.payLoanConfirmation = false;
            this.loanAccount = null;
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