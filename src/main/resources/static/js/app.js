/**
 * File JavaScript principale dell'applicazione
 * Inizializza Alpine.js e registra i componenti globali
 */
document.addEventListener("alpine:init", () => {
  // Registra i componenti Alpine.js come dati globali
  Alpine.data("ticketForm", TicketForm);
  Alpine.data("ticketFilters", TicketFilters);
  Alpine.data("dashboardStats", DashboardStats);
  Alpine.data("userDropdown", UserDropdown);

  // Store globale per gestire lo stato dell'applicazione
  Alpine.store("app", {
    darkMode: localStorage.getItem("darkMode") === "true",
    notifications: [],

    toggleDarkMode() {
      this.darkMode = !this.darkMode;
      localStorage.setItem("darkMode", this.darkMode);

      // Applica o rimuovi la classe dark dal body
      if (this.darkMode) {
        document.body.classList.add("dark-mode");
      } else {
        document.body.classList.remove("dark-mode");
      }
    },

    addNotification(message, type = "info") {
      const id = Date.now();
      this.notifications.push({ id, message, type });

      // Rimuovi la notifica dopo 5 secondi
      setTimeout(() => {
        this.removeNotification(id);
      }, 5000);
    },

    removeNotification(id) {
      this.notifications = this.notifications.filter((n) => n.id !== id);
    },
  });
});

// Inizializza gli elementi UI interattivi dopo il caricamento della pagina
document.addEventListener("DOMContentLoaded", function () {
  // Inizializza tooltip Bootstrap
  const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
  if (tooltips.length) {
    Array.from(tooltips).forEach((tooltip) => new bootstrap.Tooltip(tooltip));
  }

  // Inizializza popovers Bootstrap
  const popovers = document.querySelectorAll('[data-bs-toggle="popover"]');
  if (popovers.length) {
    Array.from(popovers).forEach((popover) => new bootstrap.Popover(popover));
  }

  // Attiva la modalità dark se salvata nelle preferenze
  if (localStorage.getItem("darkMode") === "true") {
    document.body.classList.add("dark-mode");
  }
});
