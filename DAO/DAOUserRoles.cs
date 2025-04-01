using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public class DAOUserRoles : BaseEntity
    {
        [Required]
        public List<DAORoles> Roles { get; set; }
    }
}
