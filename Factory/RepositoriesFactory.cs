using my.NET.Repositories;

namespace my.NET.Factory
{
    public class RepositoriesFactory
    {
        public static IREPBase GetRepository(string repositoryName)
        {
            switch (repositoryName)
            {
                case "Address":
                    return new REPAddress();
                case "Customer":
                    return new REPCustomer();
                case "Order":
                    return new REPOrder();
                default:
                    return null;
            }
        }
    }
}
