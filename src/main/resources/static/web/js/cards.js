const { createApp } = Vue;

const options = {
    data(){
        return {
            client: {},
            clientCards: [],
            cardsDebit: [],
            cardsCredit: [],
            dateFormatter: {},
            currentDate: ""
        }
    },
    created(){
        axios.get("/api/clients/current")
        .then(res => {
            this.client = res.data;
            this.clientCards = this.client.cards;
            this.cardsDebit = this.clientCards.filter(card => card.type == "DEBIT");
            this.cardsCredit = this.clientCards.filter(card => card.type == "CREDIT");
        })
        .catch(err => console.error(err))

        this.dateFormatter = new Intl.DateTimeFormat('en-US', {
            month: '2-digit',
            year: '2-digit'
        })

        const newDate = new Date();

        // Get the year, month, and day components
        const year = newDate.getFullYear();
        const month = String(newDate.getMonth() + 1).padStart(2, '0'); // Months are zero-based, so add 1 and pad with leading zeros
        const day = String(newDate.getDate()).padStart(2, '0');

        this.currentDate = `${year}-${month}-${day}`;
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