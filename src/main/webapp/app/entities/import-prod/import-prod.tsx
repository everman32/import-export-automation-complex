import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './import-prod.reducer';
import { IImportProd } from 'app/shared/model/import-prod.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ImportProd = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const importProdList = useAppSelector(state => state.importProd.entities);
  const loading = useAppSelector(state => state.importProd.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="import-prod-heading" data-cy="ImportProdHeading">
        <Translate contentKey="accountingImportExportProductsApp.importProd.home.title">Import Prods</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="accountingImportExportProductsApp.importProd.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="accountingImportExportProductsApp.importProd.home.createLabel">Create new Import Prod</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {importProdList && importProdList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.importProd.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.importProd.arrivaldate">Arrivaldate</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.importProd.trip">Trip</Translate>
                </th>
                <th>
                  <Translate contentKey="accountingImportExportProductsApp.importProd.grade">Grade</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {importProdList.map((importProd, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${importProd.id}`} color="link" size="sm">
                      {importProd.id}
                    </Button>
                  </td>
                  <td>
                    {importProd.arrivaldate ? <TextFormat type="date" value={importProd.arrivaldate} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{importProd.trip ? <Link to={`trip/${importProd.trip.id}`}>{importProd.trip.id}</Link> : ''}</td>
                  <td>{importProd.grade ? <Link to={`grade/${importProd.grade.id}`}>{importProd.grade.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${importProd.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${importProd.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${importProd.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="accountingImportExportProductsApp.importProd.home.notFound">No Import Prods found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ImportProd;
