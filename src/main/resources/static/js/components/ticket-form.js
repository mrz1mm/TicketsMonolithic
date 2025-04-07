/**
 * Componente Alpine.js per la gestione dei form dei ticket
 * Gestisce la validazione client-side e le interazioni dinamiche
 */
const TicketForm = () => {
  return {
    formData: {
      title: "",
      description: "",
      priority: "",
      departmentId: "",
      categoryId: "",
      assignedToId: "",
    },
    errors: {},
    loading: false,

    // Controlla se ci sono campi obbligatori mancanti
    validateForm() {
      this.errors = {};

      // Validazione del titolo
      if (!this.formData.title.trim()) {
        this.errors.title = "Il titolo è obbligatorio";
      } else if (this.formData.title.length < 5) {
        this.errors.title = "Il titolo deve contenere almeno 5 caratteri";
      }

      // Validazione della descrizione
      if (!this.formData.description.trim()) {
        this.errors.description = "La descrizione è obbligatoria";
      } else if (this.formData.description.length < 10) {
        this.errors.description =
          "La descrizione deve contenere almeno 10 caratteri";
      }

      // Validazione della priorità
      if (!this.formData.priority) {
        this.errors.priority = "La priorità è obbligatoria";
      }

      // Validazione del dipartimento
      if (!this.formData.departmentId) {
        this.errors.departmentId = "Il dipartimento è obbligatorio";
      }

      return Object.keys(this.errors).length === 0;
    },

    // Gestisce l'invio del form
    submitForm() {
      if (this.validateForm()) {
        this.loading = true;
        // Il form verrà inviato normalmente grazie all'attributo action HTML
        return true;
      }
      // Impedisce l'invio del form se ci sono errori
      return false;
    },

    // Controlla se un campo è cambiato e aggiorna la validazione
    updateField(field) {
      if (this.errors[field]) {
        this.validateForm();
      }
    },

    // Reinizializza il form
    resetForm() {
      this.formData = {
        title: "",
        description: "",
        priority: "",
        departmentId: "",
        categoryId: "",
        assignedToId: "",
      };
      this.errors = {};
    },
  };
};
