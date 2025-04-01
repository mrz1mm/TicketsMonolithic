using System.ComponentModel.DataAnnotations;

namespace myMonolithic.NET.DAO
{
    public enum StatusType
    {
        Bozza,
        Aperto,
        InLavorazione,
        InAttesaDiFeedback,
        Risolto,
        Chiuso,
        Riaperto,
        Annullato,
        InEscalation
    }

    public class Status : BaseEntity
    {
        [Required]
        [EnumDataType(typeof(StatusType))]
        public string StatusName { get; set; }
    }
}
