import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './export-prod.reducer';

export const ExportProdDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const exportProdEntity = useAppSelector(state => state.exportProd.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="exportProdDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.exportProd.detail.title">ExportProd</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{exportProdEntity.id}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.exportProd.trip">Trip</Translate>
          </dt>
          <dd>{exportProdEntity.trip ? exportProdEntity.trip.id : ''}</dd>
          <dt>
            <span id="departureDate">
              <Translate contentKey="accountingImportExportProductsApp.exportProd.departureDate">Departure Date</Translate>
            </span>
          </dt>
          <dd>
            {exportProdEntity.departureDate ? (
              <TextFormat value={exportProdEntity.departureDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.exportProd.grade">Grade</Translate>
          </dt>
          <dd>{exportProdEntity.grade ? exportProdEntity.grade.description : ''}</dd>
        </dl>
        <Button tag={Link} to="/export-prod" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/export-prod/${exportProdEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ExportProdDetail;
