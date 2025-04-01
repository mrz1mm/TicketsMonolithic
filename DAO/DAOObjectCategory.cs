using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public class DAOObjectCategory : BaseEntity
    {
        [Required]
        public string CategoryName { get; set; }
    }
}
