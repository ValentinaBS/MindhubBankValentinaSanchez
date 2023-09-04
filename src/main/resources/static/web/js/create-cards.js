const { createApp } = Vue;

const options = {
    data() {
        return {
            cardType: "",
            cardColor: "",
            errorMessage: ""
        }
    },
    methods: {
        createCard() {
            if (this.cardType == "") {
                this.errorMessage = "You must select a type of card";
                return
            }
            if (this.cardColor == "") {
                this.errorMessage = "You must select a card color";
                return
            }
            this.errorMessage = ""
            Swal.fire({
                title: 'Are you sure you want to create a new card?',
                icon: 'info',
                buttonsStyling: false,
                customClass: {
                    confirmButton: 'btn primary-btn btn-lg mb-3 mb-md-0',
                    cancelButton: 'btn secondary-btn btn-lg me-md-5 mb-3 mt-2 my-md-2'
                },
                showCancelButton: true,
                confirmButtonText: 'Yes, create a new card',
                cancelButtonText: 'Cancel',
                reverseButtons: true
            }).then(result => {
                if (result.isConfirmed) {
                    axios.post('/api/clients/current/cards', `color=${this.cardColor}&type=${this.cardType}`)
                        .then(res => {
                            window.location.href = '/web/pages/cards.html'
                            this.cardType = "";
                            this.cardColor = "";
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