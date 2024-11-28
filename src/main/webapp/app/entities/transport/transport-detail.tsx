import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transport.reducer';

export const TransportDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transportEntity = useAppSelector(state => state.transport.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transportDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.transport.detail.title">Transport</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{transportEntity.id}</dd>
          <dt>
            <span id="brand">
              <Translate contentKey="accountingImportExportProductsApp.transport.brand">Brand</Translate>
            </span>
          </dt>
          <dd>{transportEntity.brand}</dd>
          <dt>
            <span id="model">
              <Translate contentKey="accountingImportExportProductsApp.transport.model">Model</Translate>
            </span>
          </dt>
          <dd>{transportEntity.model}</dd>
          <dt>
            <span id="vin">
              <Translate contentKey="accountingImportExportProductsApp.transport.vin">Vin</Translate>
            </span>
          </dt>
          <dd>{transportEntity.vin}</dd>
        </dl>
        <Button tag={Link} to="/transport" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transport/${transportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransportDetail;
