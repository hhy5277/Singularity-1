import React, { PropTypes } from 'react';
import Immutable from 'immutable';

import Line from './Line';

export const LOG_LINE_HEIGHT = 14;

const SimpleLogLines = (props) => {
  if (!props.isLoaded) {
    return <div>Not loaded</div>;
  }

  return (
    <div>
      <div
        style={{height: props.fakeLineCount * LOG_LINE_HEIGHT}}
        key="fakeLines"
      />
      {props.lines.map((data) => {
        return (
          <Line
            key={`${data.start}-${data.end}`}
            data={data}
            hrefFunc={props.hrefFunc}
          />
        );
      })}
    </div>
  );
};

SimpleLogLines.propTypes = {
  isLoaded: PropTypes.bool.isRequired,
  lines: PropTypes.instanceOf(Immutable.List).isRequired,
  fakeLineCount: PropTypes.number,
  hrefFunc: PropTypes.func
};

SimpleLogLines.defaultProps = {
  fakeLineCount: 0
};

export default SimpleLogLines;
