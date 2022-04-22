import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './transport.reducer';
import { ITransport } from 'app/shared/model/transport.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Transport = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const transportList = useAppSelector(state => state.transport.entities);
  const loading = useAppSelector(state => state.transport.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="transport-heading" data-cy="TransportHeading">
        <Translate contentKey="accountingImportExportProductsApp.transport.home.title">Transports</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="accountingImportExportProductsApp.transport.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="accountingImportExportProductsApp.transport.home.createLabel">Create new Transport</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {transportList && transportList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.transport.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.transport.brand">Brand</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.transport.model">Model</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.transport.vin">Vin</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {transportList.map((transport, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${transport.id}`} color="link" size="sm">
                      {transport.id}
                    </Button>
                  </td>
                  <td>{transport.brand}</td>
                  <td>{transport.model}</td>
                  <td>{transport.vin}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${transport.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${transport.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${transport.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="accountingImportExportProductsApp.transport.home.notFound">No Transports found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Transport;
