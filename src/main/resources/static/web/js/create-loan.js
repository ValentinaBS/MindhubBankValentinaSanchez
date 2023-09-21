const { createApp } = Vue;

const options = {
    data(){
        return {
            allLoans: [],
            loanNameInput: "",
            maxAmountInput: "",
            interestInput: "",
            installmentsInput: "",
            installmentsList: [],
            errorMessage: "",
            moneyFormatter: {}
        }
    },
    created() {
        axios.get('/api/loans')
            .then(res => {
                this.allLoans = res.data;
                console.log(this.allLoans)
            })
            .catch(error => {
                console.error("Error loading all loans:", error);
            });
            
        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        });
    },
    methods:{
        createLoan(){

            if (this.maxAmountInput == "") {
                this.errorMessage = "Please enter a maximum amount for the loan.";
                return
            }
            if (this.interestInput == "") {
                this.errorMessage = "Please enter an interest rate for the loan.";
                return
            }
            const installmentsRegex = new RegExp(/^\d+(,\s*\d+)*$/); // Only digits with commas and optional spaces.
            if (!(installmentsRegex.test(this.installmentsInput))) {
                this.errorMessage = "Please write the installments in the next format: 3, 6, 9";
                return
            }

            this.installmentsList = this.installmentsInput.split(/\s*,\s*/);

            this.modalCreateLoan();
        },
        modalCreateLoan() {
            this.errorMessage = "";
            
            Swal.fire({
                title: 'Are you sure you want to create this loan?',
                icon: 'info',
                buttonsStyling: false,
                customClass: {
                    confirmButton: 'btn primary-btn btn-lg mb-3 mb-md-0',
                    cancelButton: 'btn secondary-btn btn-lg me-md-5 mb-3 mt-2 my-md-2'
                },
                showCancelButton: true,
                confirmButtonText: 'Yes, create loan',
                cancelButtonText: 'Cancel',
                reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    axios.post('/api/loans/create', { name: this.loanNameInput, maxAmount: this.maxAmountInput, percentage: this.interestInput, payments: this.installmentsList})
                    .then(() => {
                        Swal.fire({
                            position: 'center',
                            icon: 'success',
                            title: 'The loan has been created!',
                            showConfirmButton: true,
                            buttonsStyling: false,
                            customClass: {
                                confirmButton: 'btn primary-btn btn-lg',
                            }
                        })

                        document.location.reload()
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