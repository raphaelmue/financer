import {Amount}   from '../../../../.openapi';
import {BaseType} from 'antd/lib/typography/Base';

const positiveColor = '#3f8600';
const negativeColor = '#cf1322';

export default class AmountUtil {

    static getColor(amount: Amount): string {
        return amount.amount < 0 ? negativeColor : positiveColor;
    }

    static getTextType(amount: Amount): BaseType {
        return amount.amount < 0 ? 'danger' : 'success';
    }

}
