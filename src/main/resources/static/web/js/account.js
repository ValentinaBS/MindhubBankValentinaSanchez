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
            moneyFormatter: {}
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
            })
            .catch(err => {
                window.location.href = '/web/pages/accounts.html'
            })

        this.moneyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        })
    },
    methods: {
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