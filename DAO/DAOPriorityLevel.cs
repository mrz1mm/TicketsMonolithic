using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public enum PriorityLevel
    {
        Bassa,
        Media,
        Alta,
        Critica
    }

    public class DAOPriorityLevel : BaseEntity
    {
        [Required]
        [EnumDataType(typeof(PriorityLevel))]
        public string PriorityName { get; set; }
    }
}
