package com.heaton.funnyvote.database;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.heaton.funnyvote.database.Option;
import com.heaton.funnyvote.database.Promotion;
import com.heaton.funnyvote.database.User;
import com.heaton.funnyvote.database.VoteData;

import com.heaton.funnyvote.database.OptionDao;
import com.heaton.funnyvote.database.PromotionDao;
import com.heaton.funnyvote.database.UserDao;
import com.heaton.funnyvote.database.VoteDataDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig optionDaoConfig;
    private final DaoConfig promotionDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig voteDataDaoConfig;

    private final OptionDao optionDao;
    private final PromotionDao promotionDao;
    private final UserDao userDao;
    private final VoteDataDao voteDataDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        optionDaoConfig = daoConfigMap.get(OptionDao.class).clone();
        optionDaoConfig.initIdentityScope(type);

        promotionDaoConfig = daoConfigMap.get(PromotionDao.class).clone();
        promotionDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        voteDataDaoConfig = daoConfigMap.get(VoteDataDao.class).clone();
        voteDataDaoConfig.initIdentityScope(type);

        optionDao = new OptionDao(optionDaoConfig, this);
        promotionDao = new PromotionDao(promotionDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        voteDataDao = new VoteDataDao(voteDataDaoConfig, this);

        registerDao(Option.class, optionDao);
        registerDao(Promotion.class, promotionDao);
        registerDao(User.class, userDao);
        registerDao(VoteData.class, voteDataDao);
    }
    
    public void clear() {
        optionDaoConfig.clearIdentityScope();
        promotionDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
        voteDataDaoConfig.clearIdentityScope();
    }

    public OptionDao getOptionDao() {
        return optionDao;
    }

    public PromotionDao getPromotionDao() {
        return promotionDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public VoteDataDao getVoteDataDao() {
        return voteDataDao;
    }

}