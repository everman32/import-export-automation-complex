import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './positioning.reducer';

export const PositioningDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const positioningEntity = useAppSelector(state => state.positioning.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="positioningDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.positioning.detail.title">Positioning</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{positioningEntity.id}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="accountingImportExportProductsApp.positioning.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{positioningEntity.latitude}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="accountingImportExportProductsApp.positioning.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{positioningEntity.longitude}</dd>
        </dl>
        <Button tag={Link} to="/positioning" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/positioning/${positioningEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PositioningDetail;
