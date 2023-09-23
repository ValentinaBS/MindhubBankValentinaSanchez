const { createApp } = Vue;

const options = {
    data() {
        return {
            accountId: null,
            chosenAccount: {},
            transactions: [],
            urlParams: {},
            debitClass: 'debit',
            creditClass: 'credit',
            dateStartInput: '',
            dateEndInput: '',
            errorMessage: '',
            moneyFormatter: {},
            loading: true
        }
    },
    created() {
        this.urlParams = new URLSearchParams(location.search);
        this.accountId = this.urlParams.get("id");

        axios.get(`/api/accounts/${this.accountId}`)
            .then(res => {
                this.chosenAccount = res.data;
                this.transactions = this.chosenAccount.transactions.sort((a, b) => {
                    return (a.transferDate > b.transferDate) ? -1 : ((a.transferDate < b.transferDate) ? 1 : 0);
                });
                console.log(this.transactions)
            })
            .catch(err => {
                window.location.href = '/web/pages/accounts.html'
            })

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        })

        setTimeout(() => { 
            this.loading = false;
        }, 1000)
    },
    methods: {
        downloadTransactions() {

            if (this.dateStartInput == "" || this.dateEndInput == "") {
                this.errorMessage = "Please enter a start and ending date.";
                return
            }

            this.dateStartInput = this.dateStartInput.replace('T', ' ');
            this.dateEndInput = this.dateEndInput.replace('T', ' ');

            axios.get('/api/transactions' + `?dateStart=${this.dateStartInput}&dateEnd=${this.dateEndInput}&accountNumber=${this.chosenAccount.number}`, 
                {responseType: "blob"}) // To recieve binary data
                .then(res => {
                    // Creates a Blob object with the PDFs content and creates a URL
                    let blob = new Blob([res.data], { type: "application/pdf" });
                    let url = window.URL.createObjectURL(blob);

                    // Creates a link to download the PDF
                    let a = document.createElement("a");
                    a.href = url;
                    a.download = "Transactions_Mindhub_Bank.pdf";

                    // Simulates a click to start the download
                    document.body.appendChild(a);
                    a.click();

                    // Cleans the URL object
                    window.URL.revokeObjectURL(url);

                    this.dateStartInput = '';
                    this.dateEndInput = '';
                    this.errorMessage = '';
                })
                .catch(error => {
                    const reader = new FileReader();

                    reader.onload = (e) => {
                        this.errorMessage = e.target.result;
                    };// Read the Blob as text

                    reader.readAsText(error.response.data);

                    console.log(error.response.data.text()
                        .then(res=>{
                            console.log(res);
                        }));
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