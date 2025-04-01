using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public enum RolesType
    {
        Admin,
        SubAdmin,
        NormalUser,
        Employee,
        Guest
    }

    public class DAORoles : BaseEntity
    {
        [Required]
        [EnumDataType(typeof(RolesType))]
        public string Role { get; set; }
    }
}
