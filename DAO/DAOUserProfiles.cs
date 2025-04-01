using my.NET.DAO;

namespace myMonolithic.NET.DAO
{
    public class DAOUserProfiles : BaseEntity
    {
        public string Name { get; set; }
        public string Surname { get; set; }
        public List<DAOAddresses> Address { get; set; }
        public string PhoneNumber { get; set; }

        public List<DAOAddresses> GetAddress(string AndWhere = "AND Name = 'Paolo'")
        {
            //public string Query = $"SELECT * FROM Anagraphic WHERE PKId = ";

            if (AndWhere != "")
            {
                //Query += AndWhere;
            }

            Address = Address.Where(a => a.UserProfileId == PKID).ToList();

            return Address;
        }
    }
}
