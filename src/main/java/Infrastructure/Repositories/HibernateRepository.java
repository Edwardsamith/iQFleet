package Infrastructure.Repositories;


import Domain.Entities.Entity;
import Domain.Exceptions.ApplicationsExepctions;
import Domain.Repositories.Repository;
import Infrastructure.Persistence.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class HibernateRepository<T extends Entity>
        implements Repository<T> {

    private final Class<T> entityClass;

    protected HibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {

        Transaction transaction = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.persist(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public void update(T entity) {

        Transaction transaction = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.merge(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public void delete(UUID id) throws ApplicationsExepctions {

        Transaction transaction = null;

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            T entity = session.find(entityClass, id);

            if (entity != null) {
                session.remove(entity);
                transaction.commit();
            }

            throw new ApplicationsExepctions("Entity with id " + id + " not found");

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public Optional<T> findById(UUID id) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.find(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<T> findAll() {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "FROM " + entityClass.getSimpleName(),
                    entityClass
            ).list();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }
}