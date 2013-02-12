package org.mongolink;

import com.mongodb.DB;
import org.mongolink.domain.UnitOfWork;
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
            LOGGER.debug("Start a new consistent request");
            db.requestStart();
            db.requestEnsureConnection();
            return STARTED;
        }
    }, STARTED {
        @Override
        public SessionState stop(final DB db, final UnitOfWork unitOfWork) {
            unitOfWork.flush();
            db.requestDone();
            LOGGER.debug("End the current consistent request");
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

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionState.class);
}
