package com.merxport.trading.serviceImpl;

import com.merxport.trading.entities.PersistingBaseEntity;
import com.merxport.trading.entities.User;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.exception.EntityNotFoundException;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
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
            update.set("audit.archivedBy", "System");
            update.set("audit.archivedDate", now);
            update.set("audit.modifiedBy", t.getAudit().getModifiedBy());
            update.set("audit.modifiedDate", t.getAudit().getModifiedDate());
            UpdateResult updateResult = mongoTemplate.updateFirst(q, update, User.class);
            if (updateResult.wasAcknowledged())
            {
                t = mongoRepository.findById(t.getId()).orElse(null);
                assert t != null;
                t.getAudit().setArchivedDate(now);
                t.getAudit().setArchivedBy("System");
                t.getAudit().setModifiedDate(now);
                t.getAudit().setModifiedBy("System");
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
