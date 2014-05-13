package org.mongolink.domain.session;

import com.mongodb.DB;
import org.mongolink.MongoLinkError;
import org.mongolink.MongoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum SessionState {
    NOTSTARTED {
        @Override
        public SessionState stop(final DB db, final UnitOfWork unitOfWork) {
            throw new MongoLinkError("Session not started");
        }

        @Override
        public SessionState start(final DB db) {
            LOGGER.debug("Starting");
            db.requestStart();
            db.requestEnsureConnection();
            return STARTED;
        }
    }, STARTED {
        @Override
        public SessionState stop(final DB db, final UnitOfWork unitOfWork) {
            unitOfWork.commit();
            db.requestDone();
            LOGGER.debug("Stoping");
            return STOPPED;
        }

        @Override
        public SessionState start(final DB db) {
            throw new MongoLinkError("Session already started");
        }
    },
    STOPPED {
        @Override
        public SessionState stop(final DB db, final UnitOfWork unitOfWork) {
            throw new MongoLinkError("Session already stopped");
        }

        @Override
        public SessionState start(final DB db) {
            throw new MongoLinkError("Session stopped");
        }
    };

    public void ensureStarted() {
        if(this != STARTED) {
            throw new MongoLinkError("Session not started");
        }
    }

    public abstract SessionState stop(final DB db, final UnitOfWork unitOfWork);

    public abstract SessionState start(final DB db) ;

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSession.class);
}
