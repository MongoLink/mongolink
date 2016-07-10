package org.mongolink.domain.session;

import com.mongodb.client.MongoDatabase;
import org.mongolink.*;
import org.slf4j.*;

enum SessionState {
    NOTSTARTED {
        @Override
        public SessionState stop(final MongoDatabase db, final UnitOfWork unitOfWork) {
            throw new MongoLinkError("Session not started");
        }

        @Override
        public SessionState start(final MongoDatabase db) {
            LOGGER.debug("Starting");

            return STARTED;
        }
    }, STARTED {
        @Override
        public SessionState stop(final MongoDatabase db, final UnitOfWork unitOfWork) {
            unitOfWork.commit();
            LOGGER.debug("Stoping");
            return STOPPED;
        }

        @Override
        public SessionState start(final MongoDatabase db) {
            throw new MongoLinkError("Session already started");
        }
    },
    STOPPED {
        @Override
        public SessionState stop(final MongoDatabase db, final UnitOfWork unitOfWork) {
            throw new MongoLinkError("Session already stopped");
        }

        @Override
        public SessionState start(final MongoDatabase db) {
            throw new MongoLinkError("Session stopped");
        }
    };

    public void ensureStarted() {
        if(this != STARTED) {
            throw new MongoLinkError("Session not started");
        }
    }

    public abstract SessionState stop(final MongoDatabase db, final UnitOfWork unitOfWork);

    public abstract SessionState start(final MongoDatabase db) ;

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSession.class);
}
