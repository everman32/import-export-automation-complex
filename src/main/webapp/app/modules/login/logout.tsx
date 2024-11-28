import React, { useLayoutEffect } from 'react';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { logout } from 'app/shared/reducers/authentication';
import { Translate } from 'react-jhipster';

export const Logout = () => {
  const authentication = useAppSelector(state => state.authentication);
  const dispatch = useAppDispatch();

  useLayoutEffect(() => {
    dispatch(logout());
    if (authentication.logoutUrl) {
      window.location.href = authentication.logoutUrl;
    } else if (!authentication.isAuthenticated) {
      window.location.href = '/';
    }
  });

  return (
    <div className="p-5">
      <h4>
        <Translate contentKey="home.loggedout.message">Logged out successfully!</Translate>
      </h4>
    </div>
  );
};

export default Logout;
