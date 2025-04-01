using my.NET.DAO;
using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public class DAODepartments : BaseEntity
    {
        [Required]
        public DepartmentsType DepartmentName { get; set; }

        [Required]
        public string? Description { get; set; }

        [Required]
        public string Email { get; set; }

        [Required]
        public string PhoneNumber { get; set; }
    }
}
