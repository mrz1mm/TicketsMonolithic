using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public class DAOTickets : BaseEntity
    {
        [Required]
        public string TicketName { get; set; }

        [Required]
        public DAOUserProfiles Requester { get; set; }

        [Required]
        public DAOObjectCategory Category { get; set; }

        [Required]
        public string TicketDescription { get; set; }


        // NULLABLE
        public DAOPriorityLevel? Priority { get; set; }
        public DAOUserProfiles? AssignedTo { get; set; }
        public byte[]? Attachment { get; set; }
        public string? Response { get; set; }

    }
}
