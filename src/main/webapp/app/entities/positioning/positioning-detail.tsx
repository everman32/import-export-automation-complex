import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './positioning.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PositioningDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
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
