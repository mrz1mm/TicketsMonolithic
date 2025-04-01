namespace my.NET.Repositories
{
    public interface IREPBase<T>
    {
        public T GetById(int PKId);
        public T Save(T entity);
        public void Remove(int PKId);
        public List<T> Read(string Query);
    }
}
