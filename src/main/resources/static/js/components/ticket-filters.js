/**
 * Componente Alpine.js per gestire i filtri dei ticket
 * Permette di filtrare dinamicamente le liste di ticket
 */
const TicketFilters = () => {
  return {
    filters: {
      status: "",
      priority: "",
      department: "",
      category: "",
      assignee: "",
      dateRange: "",
    },
    showFilters: false,

    // Inizializza i filtri dagli URL parameters se presenti
    init() {
      const urlParams = new URLSearchParams(window.location.search);

      // Popola i filtri dai parametri URL
      if (urlParams.has("status"))
        this.filters.status = urlParams.get("status");
      if (urlParams.has("priority"))
        this.filters.priority = urlParams.get("priority");
      if (urlParams.has("department"))
        this.filters.department = urlParams.get("department");
      if (urlParams.has("category"))
        this.filters.category = urlParams.get("category");
      if (urlParams.has("assignee"))
        this.filters.assignee = urlParams.get("assignee");
      if (urlParams.has("dateRange"))
        this.filters.dateRange = urlParams.get("dateRange");
    },

    // Applica i filtri e aggiorna l'URL
    applyFilters() {
      const url = new URL(window.location.href);
      const params = new URLSearchParams();

      // Aggiungi solo i filtri non vuoti ai parametri URL
      Object.entries(this.filters).forEach(([key, value]) => {
        if (value) params.set(key, value);
      });

      // Mantieni il parametro di paginazione se presente
      if (url.searchParams.has("page")) {
        params.set("page", url.searchParams.get("page"));
      }

      // Aggiorna l'URL e ricarica la pagina
      window.location.search = params.toString();
    },

    // Reimposta tutti i filtri
    resetFilters() {
      this.filters = {
        status: "",
        priority: "",
        department: "",
        category: "",
        assignee: "",
        dateRange: "",
      };

      // Applica i filtri reimpostati
      this.applyFilters();
    },

    // Verifica se ci sono filtri attivi
    hasActiveFilters() {
      return Object.values(this.filters).some((value) => !!value);
    },

    // Toggle pannello filtri
    toggleFilters() {
      this.showFilters = !this.showFilters;
    },
  };
};
