import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './trip.reducer';
import { ITrip } from 'app/shared/model/trip.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Trip = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const tripList = useAppSelector(state => state.trip.entities);
  const loading = useAppSelector(state => state.trip.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="trip-heading" data-cy="TripHeading">
        <Translate contentKey="accountingImportExportProductsApp.trip.home.title">Trips</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="accountingImportExportProductsApp.trip.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="accountingImportExportProductsApp.trip.home.createLabel">Create new Trip</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {tripList && tripList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.distance">Distance</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.transport">Transport</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.driver">Driver</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.address">Address</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.product">Product</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.trip.user">User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {tripList.map((trip, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${trip.id}`} color="link" size="sm">
                      {trip.id}
                    </Button>
                  </td>
                  <td>{trip.distance}</td>
                  <td>{trip.transport ? <Link to={`transport/${trip.transport.id}`}>{trip.transport.id}</Link> : ''}</td>
                  <td>{trip.driver ? <Link to={`driver/${trip.driver.id}`}>{trip.driver.id}</Link> : ''}</td>
                  <td>{trip.address ? <Link to={`address/${trip.address.id}`}>{trip.address.id}</Link> : ''}</td>
                  <td>{trip.product ? <Link to={`product/${trip.product.id}`}>{trip.product.id}</Link> : ''}</td>
                  <td>{trip.user ? trip.user.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${trip.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${trip.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${trip.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="accountingImportExportProductsApp.trip.home.notFound">No Trips found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Trip;
