import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './import-prod.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ImportProdDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const importProdEntity = useAppSelector(state => state.importProd.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="importProdDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.importProd.detail.title">ImportProd</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{importProdEntity.id}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.importProd.trip">Trip</Translate>
          </dt>
          <dd>{importProdEntity.trip ? importProdEntity.trip.id : ''}</dd>
          <dt>
            <span id="arrivalDate">
              <Translate contentKey="accountingImportExportProductsApp.importProd.arrivalDate">Arrival Date</Translate>
            </span>
          </dt>
          <dd>
            {importProdEntity.arrivalDate ? <TextFormat value={importProdEntity.arrivalDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.importProd.grade">Grade</Translate>
          </dt>
          <dd>{importProdEntity.grade ? importProdEntity.grade.description : ''}</dd>
        </dl>
        <Button tag={Link} to="/import-prod" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/import-prod/${importProdEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ImportProdDetail;
