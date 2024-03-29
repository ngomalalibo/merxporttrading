package com.merxport.trading.serviceImpl;

import com.merxport.trading.entities.PersistingBaseEntity;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.exception.EntityNotFoundException;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class DeleteServiceImpl
{
    public <T extends PersistingBaseEntity> T deleteEntity(T t, MongoTemplate mongoTemplate, MongoRepository<T, String> mongoRepository)
    {
        if (!Objects.isNull(t))
        {
            LocalDateTime now = LocalDateTime.now();
            Query q = new Query(Criteria.where("_id").is(t.getId()));
            Update update = new Update();
            update.set("isActive", false);
            update.set("audit.archivedBy", t.getSessionUser());
            update.set("audit.archivedDate", now);
            update.set("audit.modifiedBy", t.getSessionUser());
            update.set("audit.modifiedDate", now);
            UpdateResult updateResult = mongoTemplate.updateFirst(q, update, t.getClass());
            if (updateResult.wasAcknowledged() && updateResult.getMatchedCount() == 1)
            {
                t = mongoRepository.findById(t.getId()).orElse(null);
                assert t != null;
                t.getAudit().setArchivedDate(now);
                t.getAudit().setArchivedBy(t.getSessionUser());
                t.getAudit().setModifiedDate(now);
                t.getAudit().setModifiedBy(t.getSessionUser());
                return t;
            }
            else
            {
                throw new CustomNullPointerException(t.getClass().getSimpleName() + " not deleted");
            }
            // userRepository.deleteById(id);
            // gridFsTemplate.delete(new Query(Criteria.where("_id").is(user.getFileID())));
        }
        else
        {
            throw new EntityNotFoundException("User not found");
        }
    }
}
