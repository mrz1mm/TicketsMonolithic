namespace myMonolithic.NET.DAO
{
    public class DAOComments : BaseEntity
    {
        public DAOTickets Ticket { get; set; }
        public DAOUserProfiles Commenter { get; set; }
        public string Content { get; set; }
    }
}
